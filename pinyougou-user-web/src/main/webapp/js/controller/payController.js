app.controller('payController',function($scope,$location,payService){

    $scope.init = function(){
        $scope.out_trade_no = $location.search()['out_trade_no'];
        $scope.totalFee = $location.search()['totalFee'];
        $scope.generateNativePayOR();
    
    }

    $scope.generateNativePayOR = function(){
        payService.generateNativePayOR().success(
            function(response){
                $scope.code_url = response.code_url;
                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    size:250,
                    level:'L',
                    value:$scope.code_url
                })
                $scope.out_trade_no = response.out_trade_no;
                $scope.total_fee = (response.total_fee/100).toFixed(2);
                $scope.getPayResult($scope.out_trade_no);
            }
        )
    }

    $scope.getPayResult = function(out_trade_no){
        payService.getPayResult(out_trade_no).success(
            function(response){
                if(response.success){
                    location.href = 'home-index.html';
                }else{
                    if(response.message=='支付失败'){
                        location.href='payfail.html';
                    }else{
                        $scope.generateNativePayOR();
                    }
                }
            }
        )
    }
})