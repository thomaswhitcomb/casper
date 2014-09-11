/usr/local/Cellar/tomcat/8.0.9/bin/catalina stop
lein clean
lein ring uberwar abc.war
rm -Rf /usr/local/Cellar/tomcat/8.0.9/libexec/webapps/abc*
cp target/abc.war /usr/local/Cellar/tomcat/8.0.9/libexec/webapps
/usr/local/Cellar/tomcat/8.0.9/bin/catalina start

