define(['jquery', 'underscore', 'Analytics'],
  function($, _, Analytics) {

    var Controller_ResendVerification = function() {
      $('#button-resend').click(this.onClick.bind(this));
    }

    Controller_ResendVerification.prototype = {
      /**
       *
       */
      onClick: function(event) {
        event.preventDefault();
        event.stopPropagation();

        $('#button-resend')
          .addClass('loading');
        $('#button-resend').parent()
          .removeClass('sent');

        $.ajax('/api/resend_verification', {
          data: {  },
          dataType: 'json',
          cache: false,
          timeout: 8000,
          success: this.onHttpSuccess.bind(this),
          error: this.onHttpError.bind(this)
        }).then(function(event) {
          $('#button-resend')
            .removeClass('loading');
          $('#button-resend').parent()
            .addClass('sent');
        });

        Analytics.action('verification', 'resend');
      },

      /**
       *
       */
      onHttpError: function(request, status, errorThrown) {
        console.log("Request Failed with message: " + errorThrown);
      },

      /**
       *
       */
      onHttpSuccess: function(data, textStatus, jqXHR) {
        if(data.success) {
          this.onSuccess(data);
        } else {
          this.onError(data);
        }
      },

      /**
       *
       */
      onSuccess: function(data) {
        console.log(data);
      },

      /**
       *
       */
      onError: function(data) {
        console.error(data);
      }

    };

    return Controller_ResendVerification;
  }
);
