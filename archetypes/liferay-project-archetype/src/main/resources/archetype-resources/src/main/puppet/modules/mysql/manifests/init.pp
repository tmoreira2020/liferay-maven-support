class mysql {
  include params

  package { "mysql-server":
    ensure => "installed";
  }

  file { "/etc/my.cnf":
    name   => "/etc/my.cnf",
    ensure => present,
    source => "puppet:///modules/mysql/my.cnf",
    owner  => "root",
    group  => "root",
    mode   => 0644,
    notify  => Service["mysqld"]
  }

  service { "mysqld":
    ensure  => "running",
    require => [Package["mysql-server"],File["/etc/my.cnf"]],
  }

  exec { "mysqladmin -u root":
    path    => "/usr/bin",
    command => "mysqladmin -uroot password '$params::mysql_root_password'",
    require => Service["mysqld"],
  }

  exec { "mysql create user $params::mysql_username":
    path    => "/usr/bin",
    command => "mysql -uroot -p$params::mysql_root_password -e \"GRANT ALL PRIVILEGES ON lportal.* TO $params::mysql_username@localhost IDENTIFIED BY '$params::mysql_password'\"",
    require => Exec["mysqladmin -u root"],
  }

  exec { "mysql create database lportal":
    path    => "/usr/bin",
    command => "mysql -u$params::mysql_username -p$params::mysql_password -e \"CREATE DATABASE lportal CHARSET UTF8\"",
    require => Exec["mysql create user $params::mysql_username"],
  }
}