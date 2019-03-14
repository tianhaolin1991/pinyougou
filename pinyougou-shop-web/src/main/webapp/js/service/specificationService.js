app.service('specificationService',function($http){
	
	this.findOne = function(specId){
		return $http.get('../specification/findOne.do?specId=' + specId);
	}
})