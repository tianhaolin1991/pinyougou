//首页控制器
app.controller('indexController',function($scope,loginService,orderService){

	//变量声明
	$scope.status ='';//订单状态
	$scope.orderList = [];//订单列表
	$scope.pageNum = 1;
	$scope.pageSize = 3;
	

	$scope.getUsername=function(){
			loginService.getUsername().success(
					function(response){
						$scope.loginName=response.username;
					}
			);
	}
	
	//根据订单状态查找订单集合
	$scope.findOrderListByUserIdAndStatus = function(){
		orderService.findOrderListByUserIdAndStatus($scope.status,$scope.pageNum,$scope.pageSize).success(
			function(response){
				$scope.orderList = response.rows;
				$scope.total = response.total;
				$scope.totalPage = Math.ceil($scope.total/$scope.pageSize);
				updatePageList();
			}
		)
	}

	//计算分页数据
	
	$scope.updatePageData = function(pageNum){
		$scope.pageNum = pageNum;
		if($scope.pageNum<=0){
			$scope.pageNum=1;
		}
		if($scope.pageNum>=$scope.totalPage){
			$scope.pageNum = $scope.totalPage;
		}
		$scope.findOrderListByUserIdAndStatus();
	}

	//更新分页条
	updatePageList = function(){
		
		$scope.pageList = [];//分页条
		var start = $scope.pageNum - 2;
		var end = $scope.pageNum + 2;
		if(start<=0){
			start = 1;
			end = 5;
			if(end > $scope.totalPage){
				end = $scope.totalPage;
			}
		}
		if(end>$scope.totalPage){
			end = $scope.totalPage;
			start = end - 4;
			if(start<=0){
				start = 1;
			}
		}

		for(var i = start;i<= end; i++){
			$scope.pageList.push(i);
		}
	}


	$scope.pay = function(orderId,totalFee){
		orderService.addPayLogFromUserOrderCenter(orderId).success(
			function(response){
				if(response.success){
					$scope.out_trade_no = response.message;
					location.href='pay.html#?out_trade_no='+ $scope.out_trade_no +'&totalFee='+totalFee*100;
				}else{
					alert(response.message);
				}
				
			}
		)
	}

	$scope.transactionConfirm = function(orderId){
		orderService.transactionConfirm(orderId).success(
			function(response){
				if(response.success){
					$scope.findOrderListByUserIdAndStatus();
				}else{
					alert(response.message);
				}
			}
		)
	}

	$scope.transactionCancel = function(orderId){
		orderService.transactionCancel(orderId).success(
			function(response){
				if(response.success){
					$scope.findOrderListByUserIdAndStatus();
				}else{
					alert(response.message);
				}
			}
		)
	}

	//更新status
	$scope.updateStatus = function(status){
		$scope.status = status;
		$scope.findOrderListByUserIdAndStatus();
	}
});