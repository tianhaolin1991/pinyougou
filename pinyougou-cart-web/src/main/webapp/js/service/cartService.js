app.service('cartService',function($http){

    this.findCartList = function(){
        return $http.get('cart/findCartList.do');
    }

    this.addItemToCartList = function(itemId,num){
        return $http.get('cart/addItemToCartList.do?itemId='+ itemId +'&num=' + num);
    }


    this.getTotal = function(cartList){
        var total={};
        var totalPrice = 0;
        var totalNum = 0;
        for(var i = 0 ; i < cartList.length ; i++){
            for(var j = 0 ; j< cartList[i].itemList.length;j++){
                totalPrice += cartList[i].itemList[j].totalFee;
                totalNum += cartList[i].itemList[j].num;
            }
        }
        total.totalPrice = totalPrice;
        total.totalNum = totalNum;
        return total;
    }

    this.findAddressListByLoginUser = function(){
        return $http.get('address/findAddressListByLoginUser.do');
    }
})