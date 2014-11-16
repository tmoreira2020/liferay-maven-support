class groups {
  group { "www":
    ensure     => present,
    gid        => "510"
  }
}