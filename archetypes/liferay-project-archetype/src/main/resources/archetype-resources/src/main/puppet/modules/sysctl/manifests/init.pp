class sysctl {
  exec { "sysctl -w vm.overcommit_memory=1":
    path    => "/sbin",
    command => "sysctl -w vm.overcommit_memory=1",
    timeout => 600,
  }
}