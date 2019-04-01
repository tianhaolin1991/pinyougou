app.controller('cartController',function($scope,cartService,orderService){

    $scope.findCartList = function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList = response;
                $scope.getTotal();
            }
        )
    }

    $scope.addItemToCartList = function(itemId,num){
        cartService.addItemToCartList(itemId,num).success(
            function(response){
                if(response.success){
                    $scope.findCartList();
                }else{
                    alert(response.message);
                }
            }
        )
    }

    $scope.getTotal = function(){
        var total = cartService.getTotal($scope.cartList);
        $scope.totalPrice = total.totalPrice;
        $scope.totalNum = total.totalNum;
    }

    $scope.findAddressListByLoginUser = function(){
        cartService.findAddressListByLoginUser().success(
            function(response){
                $scope.addressList = response;
                for(var i=0;i<response.length;i++){
                    if(response[i].isDefault=='1'){
                        $scope.address = response[i];
                        break;
                    }
                }
            }
        )
    }

    $scope.selectAddress = function(address){
        $scope.address = address;
    }

    $scope.isSelectedAddress = function(address){
        if($scope.address==address){
            return true;
        }
        return false;
    }

    $scope.order = {paymentType:'1'};

    $scope.selectPaymentType = function(type){
        $scope.order.paymentType = type;
    }


    $scope.addOrder = function(){
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;
        orderService.add($scope.order).success(
            function(response){
                if(response.success){
                    if($scope.order.paymentType == '1'){
                        location.href = "pay.html"
                    }else{
                        location.href = "paysuccess.html"
                    }
                }
            }
        )
    }

})