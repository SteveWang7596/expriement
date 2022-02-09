attrib /S -R ..\src\*.*
rmdir /S ..\src\gen\java
xcopy /Q /Y /S .\client-server\src\main\java ..\src\gen\java
xcopy /Q /Y cardpaymentapi.yaml ..\src\resources