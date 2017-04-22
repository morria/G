require.config({
  baseUrl: '/js',
  paths: {
    'jquery': 'vendor/jquery-2.0.3.min',
    'underscore': 'vendor/underscore-1.5.2.min',
    'bootstrap': 'vendor/bootstrap.min',
    'mustache': 'vendor/mustache',
    'ga': [
      '//www.google-analytics.com/analytics',
      'vendor/analytics'
     ]
  },
  shim: {
    jquery: {
      exports: 'jQuery'
    },
    underscore: {
      exports: '_'
    },
    mustache: {
      exports: 'mustache'
    },
    bootstrap: ['jquery'],
    ga: {
      exports: 'ga'
    }
  }
})

require(['Controller/CreateLink'],
  function(Controller_CreateLink) {
    new Controller_CreateLink();
  }
)
