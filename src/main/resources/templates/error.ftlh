<!DOCTYPE html>
<html lang="ja-JP">
<#include "include-header.ftlh">

<body>
	<style>
		.bg-dark {
			background-color: #000000 !important;
		}
	</style>
	<div>
		<nav class="navbar navbar-dark bg-dark fixed-top" role="navigation">
			<div class="container-fluid">
				<a class="navbar-brand effect-shine" style="font-size: 21px;font-weight: 900;"
					href="/category/login.action">
					<img src="/static/image/cross.svg" alt="img01" width="42px" height="42px">
					NASB1995
				</a>
			</div>
			<form id="logoutForm" method="post" action="/category/logout" style="display: none;"></form>
		</nav>
	</div>
	<div class="container" style="margin-top: 60px;">
		<div class="text-center">
			<h2>システム情報</h2>
			<h6>${exception}</h6>
			<button id="backBtn" style="width: 300px; margin: 0px auto 0px auto;"
				class="btn btn-lg btn-warning btn-block">戻る</button>
		</div>
	</div>
	<script type="text/javascript">
		$("#backBtn").on('click', function () {
			$("#logoutForm").submit();
		});
	</script>
</body>

</html>