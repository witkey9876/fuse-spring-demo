## maven相关
1. tools包含maven lib
2. 修改setting.xml指定jar安装目录
3. 执行maven_install.sh 可下载依赖到指定目录(可修改),默认安装在
tools/local_repo目录下
4. 如果需要下载其他版本或依赖，只需要在pom.xml中添加即可
然后执行maven_install.sh
## Fuse
1. fuse version 7.6.0.fuse-760027-redhat-00001
2. 具体版本可查看https://maven.repository.redhat.com/ga/org/jboss/redhat-fuse

## MQ
1. ActiveMQ lib 用于测试
2. ibm.mq.version 为9.1.5.0，需要查看服务端的版本确定最终版本

## JDK
1. 版本需要1.8以上

##Camel

