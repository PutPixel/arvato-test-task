(function(angular) {
	var ClientFactory = function($resource) {
		return $resource('/clients/:id', {
			id : '@id'
		});
	};

	ClientFactory.$inject = [ '$resource' ];
	angular.module("myApp.services").factory("Client", ClientFactory);
}(angular));