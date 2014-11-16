class liferay {
  include params

  define download ($uri, $timeout = 300) {
    exec { "download $uri":
      path     => "/usr/bin",
      command  => "wget -q '$uri' -O $name",
      creates  => $name,
      timeout  => $timeout
    }
  }

  file { "$params::runtime_username_home":
    ensure     => "directory",
    owner      => "$params::runtime_username",
    group      => "www",
    mode       => 775
  }

  download { "/opt/liferay/liferay-portal-tomcat-6.2-ce-ga3-20150103155803016.zip":
    uri        => "http://sourceforge.net/projects/lportal/files/Liferay%20Portal/6.2.2%20GA3/liferay-portal-tomcat-6.2-ce-ga3-20150103155803016.zip",
    require    => File["$params::runtime_username_home"],
  }

  exec { "unzip /opt/liferay/liferay-portal-tomcat-6.2-ce-ga3-20150103155803016.zip":
    path       => "/usr/bin",
    command    => "unzip /opt/liferay/liferay-portal-tomcat-6.2-ce-ga3-20150103155803016.zip -d $params::runtime_username_home",
    require    => Download["/opt/liferay/liferay-portal-tomcat-6.2-ce-ga3-20150103155803016.zip"]
  }
  

  file { "$params::liferay_path":
    ensure     => "directory",
    owner      => "$params::runtime_username",
    group      => "www",
    recurse    => "true",
    mode       => 2775,
    require    => [File["$params::runtime_username_home"], Exec["unzip /opt/liferay/liferay-portal-tomcat-6.2-ce-ga3-20150103155803016.zip"]]
  }

  file { "$params::liferay_path/deploy":
    ensure     => "link",
    owner      => "$params::runtime_username",
    group      => "www",
    mode       => 2775,
    target     => "$params::runtime_username_home/deploy",
    require    => File["$params::liferay_path"],
  }

  file { "tomcat":
    name       => "/etc/init.d/tomcat",
    ensure     => present,
    content    => template("liferay/tomcat.erb"),
    owner      => "root",
    group      => "root",
    mode       => 0755,
  }

  file { "portal-ext.properties":
    name       => "$liferay_path/portal-ext.properties",
    ensure     => present,
    content    => template("liferay/portal-ext.properties.erb"),
    owner      => "$params::runtime_username",
    group      => "$params::runtime_username",
    mode       => 0755,
    require    => Exec["unzip /opt/liferay/liferay-portal-tomcat-6.2-ce-ga3-20150103155803016.zip"]
  }

  service { "tomcat":
    ensure  => "running",
    require => [Class["mysql"],File["tomcat"],File["portal-ext.properties"]],
  }
}