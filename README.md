# Datalinks backend

Datalinks backend

Run the executable with `-Dquarkus.config.locations=file:/path/to/datalinks.properties` to specify your configuration. (mandatory)

Properties example:
```
# ==============================================================================
# GENERAL APP CONFIG
# ==============================================================================
quarkus.http.host=127.0.0.1
quarkus.http.port=8007
application.sitename=Datoenlaces
application.backend.url=http://datalinks.krusher.net/datalinks-backend/
application.upload.dir=/home/krusher/datalinks-backend/uploads/
application.url=https://datalinks.krusher.net

# ReCAPTCHA integration
google.recaptcha.key.site=aslkdfjhasdklfjhasdlfkjh
google.recaptcha.key.secret=aksdjhfkasjdhfdlkfahjsd

# ==============================================================================
# LOGGING
# ==============================================================================
quarkus.log.file.path=/home/krusher/datalinks-backend/logs/datalinks.log

# ==============================================================================
# DATABASE
# ==============================================================================
quarkus.datasource.db-kind=mariadb
quarkus.datasource.jdbc.url=jdbc:mariadb://localhost:3306/datalinks?createDatabaseIfNotExist=true
quarkus.datasource.username=datalinks
quarkus.datasource.password=sladkfhsalkd

# ==============================================================================
# MAILER
# ==============================================================================
quarkus.mailer.mock=false
quarkus.mailer.from=postmaster@krusher.net
quarkus.mailer.host=ssl0.ovh.net
quarkus.mailer.port=587
quarkus.mailer.username=postmaster@krusher.net
quarkus.mailer.password=asdljkfhasdkfljhasdkfljhasdf
quarkus.mailer.auth-methods=DIGEST-MD5 CRAM-MD5 PLAIN LOGIN
quarkus.mailer.start-tls=REQUIRED

# ==============================================================================
# HIBERNATE SEARCH (Lucene Backend local)
# ==============================================================================
application.search.index-dir=/home/krusher/datalinks-backend/lucene/
```
This wis listen in localhost only on port 8007, so you can use an inverse proxy such as ``nginx`` to serve the pages. See project [datalinks-project](https://github.com/axelei/datalinks-frontend) for instructions to deploy the frontend part.