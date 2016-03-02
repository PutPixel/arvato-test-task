(function(angular) {
	var IndexController = function($scope, $resource, Client) {
		Client.query(function(response) {
			$scope.clients = response ? response : [];
		});

		$scope.parkingInfo = function(client) {
			var parkingInfo = $resource('/clients/parkedAtMonthes/:id', {
				id : client.identifier
			}).query(function() {
				client.parkingInfo = parkingInfo;
			});
		};
	};

	var ParkingInfoController = function($scope, $resource) {
		$scope.init = function(parkingInfo) {
			$scope.parkingInfo = window.parkingInfo
			$scope.invoice = window.invoice
		};
	};

	IndexController.$inject = [ '$scope', '$resource', 'Client' ];
	angular.module("myApp.controllers").controller("IndexController",
			IndexController);

	ParkingInfoController.$inject = [ '$scope', '$resource' ];
	angular.module("myApp.controllers").controller("ParkingInfoController",
			ParkingInfoController);
}(angular));