wt cmd /k "C:\intr\kafka\bin\windows\zookeeper-server-start.bat C:\intr\kafka\config\zookeeper.properties"; ^
new-tab cmd /k "C:\Installed\Instruments\keycloak-18.0.2\bin\kc.bat start-dev --http-port=8180"; ^
new-tab cmd /k "timeout /t 25 && C:\intr\kafka\bin\windows\kafka-server-start.bat C:\intr\kafka\config\server.properties"
