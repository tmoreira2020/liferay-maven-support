class iptables {
  file { "/etc/sysconfig/iptables":
    name   => "/etc/sysconfig/iptables",
    ensure => present,
    source => "puppet:///modules/iptables/iptables",
    owner  => "root",
    group  => "root",
    mode   => 0600,
    notify  => Service["iptables"]
  }

  service { "iptables":
    ensure => "running",
    hasrestart => "true",
    require => File["/etc/sysconfig/iptables"]
  }

}