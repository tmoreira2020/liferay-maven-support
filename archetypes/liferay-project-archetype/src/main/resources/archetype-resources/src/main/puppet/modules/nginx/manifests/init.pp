class nginx {
  include params

  file { "nginx.repo":
    name   => "/etc/yum.repos.d/nginx.repo",
    ensure => present,
    source => "puppet:///modules/nginx/nginx.repo",
    owner  => root,
    group  => root,
    mode   => 0640,
  }

  package {"nginx":
    ensure => "installed",
    require => File["nginx.repo"],
  }

  file { "default.conf":
    name   => "/etc/nginx/conf.d/default.conf",
    ensure => "present",
    content => template("nginx/default.conf.erb"),
    owner  => "root",
    group  => "root",
    mode   => 0755,
    require => Package["nginx"]
  }

  service { "nginx":
    ensure  => "running",
    require => File["default.conf"]
  }
}