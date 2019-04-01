app.controller('loginController',function($scope,loginService){

    $scope.getUsername =function(){
       loginService.getUsername().success(
           function(response){
               $scope.username = response.username;
           }
       )
    }

})