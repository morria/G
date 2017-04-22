define(['jquery', 'underscore', 'Analytics'],
  function($, _, Analytics) {

    var $form = $('#form-create-link');
    var $name = $form.find('input[name=name]');
    var $url = $form.find('input[name=url]');

    var Controller_CreateLink= function() {
      $form.submit(_.bind(this.onSubmit, this));
    }

    Controller_CreateLink.prototype = {

      /**
       * Attempt to create an account when the
       * form is submitted
       *
       * @return void
       */
      onSubmit: function(event) {
        event.preventDefault();
        event.stopPropagation();

        var name = $name.val();
        var url = $url.val();

        Analytics.action('create_link', 'attempt');

        $form.find('#submit')
          .addClass('loading')
          .attr('disabled', 'disabled');

        console.log("Have: ", name, url);

        $.ajax('/' + name, {
          method: "POST",
          data: { url: url },
          cache: false,
          timeout: 4000,
          success: this.onHttpSuccess.bind(this, name, url),
          error: this.onHttpError.bind(this)
        }).then(function(event) {
        })

        return false;
      },

      /**
       * On a successful response upon joining
       */
      onHttpSuccess: function(name, url, data, textStatus, jqXHR) {
        $form.find('#submit')
          .removeClass('loading')
          .attr('disabled', null);

        console.log(name, url, data, textStatus, jqXHR);

        if(data.success) {
          this.onSuccess(name, url, data);
        } else {
          this.onFail(name, url, data);
        }
      },

      /**
       * If there is an error joining
       */
      onHttpError: function(request, status, errorThrown) {
        console.log("Request Failed with message: " + errorThrown);
      },

      onSuccess: function(name, url, data) {
        Analytics.actionWithLabel('create_link', 'success', email);
      },

      onFail: function(email, data) {
          if(data.error == "...") {
          } else {
            Analytics.actionWithLabel('create_link', 'failure', email);
            console.error(data);
          }
      }
    };

    return Controller_CreateLink;
  }
);
