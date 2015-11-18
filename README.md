# Send Mail Micro Service

Implemented by Stanislav Zhurylo

== Preconditions
Java 1.8 update 45
Maven2Eclipse plugin preinstalled on Eclipse

== Import project
Import project in Eclipse: Import > Maven > Existing Maven Projects

== Build & Run application
There are 2 launch configurations available in eclipse:
Use BuildProject to build using Maven.
Use StartServer to execute as Standard Java Application

== Entrypoint
org.oecd.epms.SendMailService

== SMTP configuration
SMTP configuration params are provided to verticle during deployment.
Set your SMTP configuration and org.oecd.epms.SendMailService.

