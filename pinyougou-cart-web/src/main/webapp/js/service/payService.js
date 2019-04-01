app.service('payService',function($http){

    this.generateNativePayOR = function(){
       return $http.get('pay/generateNativePayOR.do');
    }

    this.getPayResult = function(out_trade_no){
        return $http.get('pay/getPayResult.do?out_trade_no='+ out_trade_no);
    }
})