class users {
  include params

  user { "$params::deploy_username":
    ensure     => present,
    home       => "/home/$params::deploy_username",
    shell      => "/bin/bash",
    managehome => true,
    password   => "$params::deploy_password",
    groups     => "www",
    require    => Class["groups"]
  }

  user { "nginx":
    ensure     => present,
    home       => "/var/cache/nginx",
    shell      => "/sbin/nologin",
    groups     => "www",
    require    => Class["groups"]
  }

  user { "$params::runtime_username":
    ensure     => present,
    home       => "$params::runtime_username_home",
    groups     => "www",
    uid        => "510",
    require    => Class["groups"]
  }
}