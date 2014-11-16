Liferay Project Archetype
=========
Liferay Project Archetype is a Maven archetype that creates a baseline project to build Liferay plugins. The archetype also creates the infrastructure of [Vagrant][1] + [Puppet][2] to build a VM based on VirtualBox with Liferay, [MySQL][3] and [NGinx][4] installed and configured.

# Project's Structure
The project's structure is a combination of a multi module Maven project with a Vagrant + Puppet infrastructure description.
```
|-- exts
|   `-- pom.xml
|-- hooks
|   `-- pom.xml
|-- layouts
|   `-- pom.xml
|-- pom.xml
|-- portlets
|   `-- pom.xml
|-- src
|   `-- main
|       |-- profiles
|       |   `-- development
|       |       `-- Vagrantfile
|       `-- puppet
|           |-- manifests
|           |   `-- default.pp
|           `-- modules
|               |-- groups
|               |-- hosts
|               |-- iptables
|               |-- java
|               |-- liferay
|               |-- mysql
|               |-- nginx
|               |-- params
|               |-- sudo
|               |-- sysctl
|               `-- users
|-- themes
|   `-- pom.xml
`-- webs
    `-- pom.xml
```
# VM specifications

1. Hardware
  * CentOS 6.5
  * 2 GB of RAM
  * 2 CPU cores

2. Groups
  - www

3. Users
  * liferay (www)
  * nginx (www)

4. Softwares
  * Iptables
    * `22` ssh
    * `80` http
    * `9090` and `9091` jmx
  * MySQL
    * liferay user
    * lportal database
    * accessed only through localhost interface
  * Java 1.7
    * remote jmx configured with credential `controlRole:liferay`
  * Liferay 6.2.2
    * configured to access lportal database
  * Nginx 1.6
   * configured with http proxy from port `80` to `8080`

# How to use
First step is to create a project based off the archetype `com.liferay.maven.archetypes:liferay-project-archetype`.

[1]: https://www.vagrantup.com/
[2]: https://puppetlabs.com/
[3]: http://www.mysql.com/
[4]: http://nginx.org/
