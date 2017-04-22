define([],
  function() {
    var Cookie = function() {
    }

    Cookie.prototype = {

      /**
       * Get the value of the cookie by the given name if it
       * exists, else null
       */
      get: function(name) {
        var cookies = document.cookie.split(";");
        for (var i=0; i<cookies.length; i++) {
          var cookieName = cookies[i].substr(0,cookies[i].indexOf("="));
          var value = cookies[i].substr(cookies[i].indexOf("=")+1);
          cookieName = cookieName.replace(/^\s+|\s+$/g,"");

          if(name == cookieName) {
            return JSON.parse(unescape(value));
          }
        }

        return null;
      },

      /**
       * Store a cookie with the given name and data for the given
       * number of days. The data is JSON stringified and URL encoded
       */
      store: function(name, data, expireDays, secure) {
        var date=new Date();
        date.setDate(date.getDate() + expireDays);

        var value=encodeURIComponent(data) + ((expireDays==null) ?
          "" : "; expires="+date.toUTCString());

        if(secure) {
            value += " secure";
        }

        document.cookie = name + "=" + value;
      },

      /**
       * Kill the cookie with the given name
       */
      remove: function(name) {
        this.store(name, {}, -1, true);
      }
    }

    return Cookie;
  }
);
