class sudo {
  include params

  file { "/etc/sudoers.d/$params::deploy_username":
    ensure => "file",
    content => template("sudo/sudoers.erb"),
    mode   => 440,
    owner  => "root",
    group  => "root",
  }

}