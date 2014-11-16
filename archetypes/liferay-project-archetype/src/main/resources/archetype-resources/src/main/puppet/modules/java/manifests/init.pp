class java {
  include params

  package { "java-1.7.0-openjdk-devel":
    ensure => installed;
  }

  file { "/usr/lib/jvm/java/jre/lib/management/jmxremote.access":
    name   => "/usr/lib/jvm/java/jre/lib/management/jmxremote.access",
    ensure => present,
    source => "puppet:///modules/java/jmxremote.access",
    owner  => "root",
    group  => "root",
    mode   => 0644
  }

  file { "/usr/lib/jvm/java/jre/lib/management/jmxremote.password":
    name   => "/usr/lib/jvm/java/jre/lib/management/jmxremote.password",
    ensure => present,
    source => "puppet:///modules/java/jmxremote.password",
    owner  => "root",
    group  => "root",
    mode   => 0644
  }
}