(function(angular) {
	angular.module("myApp.controllers", []);
	angular.module("myApp.services", []);
	angular.module("myApp",
			[ "ngResource", "ngRoute", "myApp.controllers", "myApp.services" ])
			.filter('groupBy', function($parse) {
				return _.memoize(function(items, field) {
					var getter = $parse(field);
					return _.groupBy(items, function(item) {
						return getter(item);
					});
				});
			});
}(angular));