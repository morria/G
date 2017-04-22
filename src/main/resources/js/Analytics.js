define(['jquery', 'ga'], function($, ga) {
  var Analytics = function() {
    var self = this;

    $(document).ready(function() {
      self.setAppId('UA-47162204-1');
      self.logPageView();

      if(email = localStorage.getItem('email')) {
        self.setUserId(email);
      }
    });
  };

  Analytics.prototype = {
    init: function() {
    },

    setAppId: function(appId) {
      ga('create', appId, 'auto');
    },

    setUserId: function(id) {
      ga('set', '&uid', id);
    },

    timing: function(category, identifier, time) {
       ga('send', 'timing', category, identifier, time);
    },

    action: function(type, description) {
      ga('send', 'event', type, description);
    },

    actionWithLabel: function(type, description, label) {
      ga('send', 'event', type, description, label);
    },

    logPageView: function() {
      ga('send', 'pageview');
    },

    pageView: function(uri) {
      ga('send', 'event', 'pageView', uri);
    }
  };

  return new Analytics();
})
