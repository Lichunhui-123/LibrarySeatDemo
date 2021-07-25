<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>图书馆预约占座管理系统</title>
    <script type="text/javascript">
    var url;
	$(function(){

		
		// 加载左树菜单
		$("#tree").tree({
			lines:true,//显示树线
			url:'menuTree.htm?parentId=-1',//请求路径
			//当数据加载成功时触发。	
			onLoadSuccess:function(){
				$("#tree").tree('expandAll');//展开所有的节点。
			},
			//当用户点击一个节点时触发
			//回调函数 node表示服务端返回的json都封装在里面
			onClick:function(node){
				//点击安全退出
				if(node.id==16){
					logout();
				}else if(node.id==15){//点击修改密码
					openPasswordUpdateDialog();
				}else if(node.attributes.menuUrl){
					openTab(node);
				}
			}
		});

		// 安全退出
		function logout(){
			$.messager.confirm('系统提示','您确定要退出系统吗？',function(r){
				if(r){
					window.location.href='logout.htm?roleId='+${currentUser.userId};
				}
			});
		}

		// 打开修改密码的窗体
		function openPasswordUpdateDialog(){
			url="updatePassword.htm";
			$("#dlg").dialog("open").dialog("setTitle","修改密码");
		}

		/**左边的菜单点击显示
			如果存在（即已经打开了），选中他
			如果不存在则打开他
		*/		
		function openTab(node){
			if($("#tabs").tabs("exists",node.text)){
				$("#tabs").tabs("select",node.text);
			}else{
				var content="<iframe frameborder=0 scrolling='auto' style='width:100%;height:100%' src="+node.attributes.menuUrl+"></iframe>"
				$("#tabs").tabs("add",{
					title:node.text,
					iconCls:node.iconCls,
					closable:true,
					content:content
				});
			}
			bindTabEvent();
		}


		bindTabEvent();
		bindTabMenuEvent();
		 
		
	});

	function bindTabEvent(){
		$(".tabs-inner").dblclick(function(){
			var subtitle = $(this).children(".tabs-title").text();
			$('#tabs').tabs('close',subtitle);
		})
		$(".tabs-inner").bind('contextmenu',function(e){
			
			$('#mm').menu('show', {
				left: e.pageX,
				top: e.pageY
			});
			var subtitle =$(this).children(".tabs-title").text();
			$('#mm').data("currtab",subtitle);
			return false;
		});
	}
			

	function bindTabMenuEvent(){
		var temp = $('#tabs');
		$('#mm-tabrefresh').click(function(){
			var currtab_title = $('#mm').data("currtab");
			var frame = temp.tabs('getTab', currtab_title).find('iframe')
			frame.attr('src',frame.attr('src'));
		});
		//关闭当前
		$('#mm-tabclose').click(function(){
			var currtab_title = $('#mm').data("currtab");
			$('#tabs').tabs('close',currtab_title);
		})
		//全部关闭
		$('#mm-tabcloseall').click(function(){
			$('.tabs-inner span').each(function(i,n){
				var t = $(n).text();
				$('#tabs').tabs('close',t);
			});	
		});
		//关闭除当前之外的TAB
		$('#mm-tabcloseother').click(function(){
			var currtab_title = $('#mm').data("currtab");
			$('.tabs-inner span').each(function(i,n){
				var t = $(n).text();
				if(t!=currtab_title)
					$('#tabs').tabs('close',t);
			});	
		});
		//关闭当前右侧的TAB
		$('#mm-tabcloseright').click(function(){
			var nextall = $('.tabs-selected').nextAll();
			if(nextall.length==0){
				//msgShow('系统提示','后边没有啦~~','error');
				alert('后边没有啦~~');
				return false;
			}
			nextall.each(function(i,n){
				var t=$('a:eq(0) span',$(n)).text();
				$('#tabs').tabs('close',t);
			});
			return false;
		});
		//关闭当前左侧的TAB
		$('#mm-tabcloseleft').click(function(){
			var prevall = $('.tabs-selected').prevAll();
			if(prevall.length==0){
				alert('到头了，前边没有啦~~');
				return false;
			}
			prevall.each(function(i,n){
				var t=$('a:eq(0) span',$(n)).text();
				$('#tabs').tabs('close',t);
			});
			return false;
		});

	}
	
	// 修改密码提交
	function updatePassword(){
		$("#fm").form("submit",{
			url:url,
			onSubmit:function(){
				var oldPassword=$("#oldPassword").val();//原密码
				var newPassword=$("#newPassword").val();//新密码
				var newPassword2=$("#newPassword2").val();//确认密码
				var reg1=/^[\w]+$/;//密码在3位到6位
				if(!$(this).form("validate")){  //先进行easyui验证
					return false;
				}
				//确认密码验证
				if(newPassword!=newPassword2){
					$.messager.alert('系统提示','确认密码输入错误！');
					return false;
				}
				//原密码验证
				if(oldPassword!='${currentUser.password}'){
					$.messager.alert('系统提示','原密码错误！');
					return false;
				}
				//密码格式
				if(!reg1.test(newPassword)){
				        $.messager.alert('系统提示','密码由数字、字母或下划线组成！');
					    return false;
				    }
				if(computedStrLen(newPassword) > 6||computedStrLen(newPassword) <3) {
					$.messager.alert('系统提示','密码3~6位！');
					return false;
				}
				
				return true;
			},
			//加载成功触发，result是服务器返回的json对象
			success:function(result){
				var result=eval('('+result+')');
				if(result.errorMsg){
					$.messager.alert('系统提示',result.errorMsg);
					return;
				}else{
					$.messager.alert('系统提示','密码修改成功，下一次登录生效！');
					//自动关闭修改密码框
					closePasswordUpdateDialog();
				}
			}
		});
	}
	
	 //计算字符串长度，英文1个字符，中文2个字符
	function  computedStrLen(str) {
		var len = 0;
		for (var i = 0; i < str.length; i++) {
			var c = str.charCodeAt(i);
			//单字节加1
			if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
				len++;
			}
			else {
				len += 2;
			}
		}
		return len;
	}

	//关闭修改密码框
	function closePasswordUpdateDialog(){
		//设置密码框的原密码、新密码、确认密码都为空
		$("#dlg").dialog("close");
		$("#oldPassword").val("");
		$("#newPassword").val("");
		$("#newPassword2").val("");
	}
</script>
  </head>
 
<body class="easyui-layout">
<!-- 顶部 -->
<div region="north" style="height: 76px; background-image: url('images/bg1.jpg')" >
<div style="padding: 0px;margin: 0px;">
<table>
	<tr>
		<td><font color="white" size="5">图书馆预约占座管理系统</font></td>
		
	</tr>
	<tr align="center">
	    <td valign="bottom" >欢迎：${currentUser.userName }&nbsp;『${currentUser.roleName }』</td>
	</tr>
</table>
</div>
</div>
<!-- 内容部分 -->
<div region="center">
    <!-- tabs选项卡 ：fit:设置选项卡容器的尺寸以适应它的父容器；border:显示选项卡（Tabs）容器边框-->
	<div id="tabs" class="easyui-tabs" fit="true" border="false"  >
		<div title="首页" data-options="iconCls:'icon-home'">
			<div align="center" style="padding-top: 100px;"><font color="gray"
			style="text-shadow: 0.1em 2px 6px gray; font-size: 80px">欢迎您!</font></div>
		</div>
	</div>
</div>


<!-- 左边的菜单树 -->
<div region="west" style="width: 160px;padding: 5px;" title="导航菜单" split="true">
<ul id="tree" class="easyui-tree"></ul>
</div>

<!-- 底部 -->
<div region="south" style="height: 25px;padding: 5px;" align="center">
	图书馆预约占座管理系统
</div>

<!-- 修改密码框 -->
<div id="dlg" class="easyui-dialog" style="width: 400px;height: 220px;padding: 10px 20px" 
 closed="true" buttons="#dlg-buttons" data-options="iconCls:'icon-modifyPassword'">
 <form id="fm" method="post">
 <input type="hidden" name="userId" id="userId" value="${currentUser.userId }">
 	<table cellspacing="4px;" style="text-align:right">
 		<tr>
 			<td>用户名：</td>
 			<td><input type="text" name="userName" id="userName" readonly="readonly" value="${currentUser.userName }" style="width: 200px;" /></td>
 		</tr>
 		<tr>
 			<td>原密码：</td>
 			<td><input type="password" class="easyui-validatebox" name="oldPassword" id="oldPassword" style="width: 200px;" required="true" /></td>
 		</tr>
 		<tr>
 			<td>新密码：</td>
 			<td><input type="password" class="easyui-validatebox" name="newPassword" id="newPassword" style="width: 200px;" required="true"  /></td>
 		</tr>
 		<tr>
 			<td>确认新密码：</td>
 			<td><input type="password" class="easyui-validatebox" name="newPassword2" id="newPassword2" style="width: 200px;" required="true" /></td>
 		</tr>
 	</table>
 </form>
</div>
<div id="dlg-buttons" style="text-align:center">
	<a href="javascript:updatePassword()" class="easyui-linkbutton" iconCls="icon-ok">保存</a>
	<a href="javascript:closePasswordUpdateDialog()" class="easyui-linkbutton" iconCls="icon-cancel">关闭</a>
</div>


<!-- 右键菜单 -->
<div id="mm" class="easyui-menu" style="width:150px;">
		<div id="mm-tabrefresh">刷新</div>
		<div class="menu-sep"></div>
		<div id="mm-tabclose">关闭</div>
		<div id="mm-tabcloseall">全部关闭</div>
		<div id="mm-tabcloseother">除此之外全部关闭</div>
		<div class="menu-sep"></div>
		<div id="mm-tabcloseright">当前页右侧全部关闭</div>
		<div id="mm-tabcloseleft">当前页左侧全部关闭</div>
</div>
	
			
		

</body>
</html>
