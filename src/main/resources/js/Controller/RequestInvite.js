define(['jquery', 'underscore', 'Analytics'],
  function($, _, Analytics) {

    var $form = $('#form-request-invite');
    var $email = $form.find('input[name=email]');

    var Controller_RequestInvite = function() {

      // Listen for form submission
      $form.submit(_.bind(this.onSubmit, this));

      $form.find('input')
        .change(_.bind(this.onInputChange, this))
        .keypress(_.bind(this.onInputChange, this))

      // If they've logged in before, set their
      // email address
      if(email = localStorage.getItem('email')) {
          $email.val(email);
      } else {
          $email.focus();
      }

      $email.bind('invalid', this.onInvalidEmail.bind(this));
    }

    Controller_RequestInvite.prototype = {

      onInputChange: function(event) {
        $form.find('.form-group').removeClass('error');
      },

      onInvalidEmail: function(event) {
        event.preventDefault();
        $email.closest('.form-group').addClass('error')
      },

      /**
       * Attempt to create an account when the
       * form is submitted
       *
       * @return void
       */
      onSubmit: function(event) {
        event.preventDefault();
        event.stopPropagation();

        var email = $email.val();

        Analytics.action('request_invite', 'attempt');

        $form.find('#submit')
          .addClass('loading')
          .attr('disabled', 'disabled');

        $.ajax('/api/send-verification', {
          data: { email: email },
          dataType: 'json',
          cache: false,
          timeout: 8000,
          success: this.onHttpSuccess.bind(this, email),
          error: this.onHttpError.bind(this)
        }).then(function(event) {
        })

        // Attempt to store their email for
        // future logins
        localStorage.setItem('email', email);
      },

      /**
       * On a successful response upon joining
       */
      onHttpSuccess: function(email, data, textStatus, jqXHR) {
        $form.find('#submit')
          .removeClass('loading')
          .attr('disabled', null);

        if(data.success) {
          this.onRequestInviteSuccess(email, data);
        } else {
          this.onRequestInviteFail(email, data);
        }
      },

      /**
       * If there is an error joining
       */
      onHttpError: function(request, status, errorThrown) {
        console.log("Request Failed with message: " + errorThrown);
      },

      onRequestInviteSuccess: function(email, data) {
        Analytics.actionWithLabel('request_invite', 'success', email);
        // ...
      },

      onRequestInviteFail: function(email, data) {
          if(data.error == "...") {
          } else {
            Analytics.actionWithLabel('request_invite', 'failure_other', email);
            console.error(data);
          }
      }
    };

    return Controller_RequestInvite;
  }
);
