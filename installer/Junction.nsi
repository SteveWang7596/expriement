!include nsDialogs.nsh
!include LogicLib.nsh

Name ${APP_NAME} ${VERSION}

# Installer attributes
XPStyle on
BGFont Arial 48 bold
BGGradient 000000 000cff ffffff
BrandingText "${APP_NAME} Junction Installer"

!define PATH_OUT "..\target"
!system 'md "${PATH_OUT}"'

OutFile ..\target\setup_${APP_NAME}-${VERSION}.${GIT_COMMIT_COUNT}.${GIT_BUILD_NR}${QUALIFIER}.exe
InstallDir "C:\TransactionJunction\${APP_NAME}"
CRCCheck on
ShowInstDetails hide

;-----------------------------------------------------------------------------------
; Pages
;-----------------------------------------------------------------------------------
Page custom Welcome ""
Page custom DatabaseDetails DatabaseDetailsEnd
Page directory
Page instfiles

Var welcome_Dialog
Var db_details_Dialog
Var db_server_hwnd
Var db_server
Var db_user_hwnd
Var db_user
Var db_password_hwnd
Var db_password

Var JRE_HOME
Var SERVICE_NAME
Var MAIN_VERTICLE_CLASS
Var NSSM
Var JMX_PORT
Var NSSM_EXE
Var JAVA_EXE
Var SVC_CP

Section - "Remove old JARs"
	Delete "$INSTDIR\lib\*.jar"
SectionEnd

Section - "Install jars"
    SetOutPath "$INSTDIR\lib"
	
	File ..\target\dependency\server-0.1.1.jar
	File ..\target\dependency\audit-0.1.1.jar
	File ..\target\dependency\client-0.1.1.jar
	File ..\target\dependency\config-0.1.1.jar
	File ..\target\dependency\info-0.1.1.jar
	File ..\target\dependency\json-schema-validator-0.1.1.jar
	File ..\target\dependency\security-0.1.1.jar
	File ..\target\dependency\utility-0.1.1.jar
    File ..\target\dependency\validator-0.1.1.jar
	File ..\target\dependency\undertow-core-1.4.0.Final.jar
	File ..\target\dependency\swagger-annotations-1.5.10.jar
	File ..\target\dependency\jboss-logging-3.2.1.Final.jar
	File ..\target\dependency\xnio-api-3.3.6.Final.jar
	File ..\target\dependency\xnio-nio-3.3.6.Final.jar

    File  "..\target\${APP_NAME}-${VERSION}${QUALIFIER}.jar"
SectionEnd

;We read defaults for these values from environmental variables to allow silent installs to work
;in cases where we don't want to use localhost and trusted auth.
Section - "Set SQL server defaults from environmental variables"
	IfSilent 0 silentcheckdone
		ReadEnvStr $db_server IAI_SQL_SERVER
		ReadEnvStr $db_user IAI_SQL_USER
		ReadEnvStr $db_password IAI_SQL_PASSWORD
	silentcheckdone:
SectionEnd

Section - "Install SQL"
	ClearErrors

	;If we are not silent then details should have been set in DatabaseDetails
    ;Correct an empty value here
    IfSilent 0 silentcheckdone
    	${If} $db_server == ""
    		StrCpy $db_server "."
    	${EndIf}
    silentcheckdone:

    SetOutPath "$INSTDIR\sql"
    File /r ..\src\main\sql\*.sql
    
    StrCpy $1 "-S $db_server"
    StrCpy $2 ""
    ${If} $db_user == ""
    	StrCpy $2 "-E"
    ${Else}
    	StrCpy $2 "-U $db_user -P $db_password"
    ${EndIf}
	
	ExecWait 'sqlcmd -b -e $1 $2 -k1 -i junction_data.sql -o junction_data.log'
    IfErrors abort 
	
    DetailPrint "Installed SQL"
    Goto end
    abort:
		IfSilent +2 0
			MessageBox MB_OK|MB_ICONSTOP "Error installing SQL - see the .log files in the sql dir.  Installation aborted prematurely." 
			
		FileOpen $4 "$INSTDIR\install_aborted.log" w
		FileWrite $4 "Error installing SQL - see the .log files in the sql dir.  Installation aborted prematurely."
		FileClose $4
		Abort
    end:
    ClearErrors
SectionEnd

Section - "Install Configuration Files"
    SetOutPath "$INSTDIR\bin\config-templates"
    File "..\resources\config.json"
	File "..\resources\logback.xml"
	File "..\resources\cluster.xml"
	File "..\resources\c3p0.properties"
SectionEnd

Section - "Install docs"
    SetOutPath "$INSTDIR\doc"
	File "..\target\docbook\en-US\*.html"
SectionEnd

Section - "Install Service"
	StrCpy $SERVICE_NAME 'Imbeko-${APP_NAME}'
	StrCpy $MAIN_VERTICLE_CLASS 'za.co.transactionjunction.imbeko.web.OpenAPIPortal'
	StrCpy $NSSM '$INSTDIR\..\Imbeko-Common\nssm-2.24'
	StrCpy $JRE_HOME '$INSTDIR\..\Imbeko-Common\jre'
	StrCpy $JMX_PORT '9054'
	
	StrCpy $NSSM_EXE '$NSSM\$SERVICE_NAME.exe'
	SetOutPath $NSSM
	CopyFiles "$NSSM\nssm.exe" "$NSSM_EXE"
	
	StrCpy $JAVA_EXE 'imbeko-${APP_NAME}-java.exe'
	SetOutPath $JRE_HOME\bin
	CopyFiles '$JRE_HOME\bin\java.exe' '$JRE_HOME\bin\$JAVA_EXE'
	
	ExecWait "$NSSM\nssm.exe stop $SERVICE_NAME"
    ExecWait "$NSSM\nssm.exe remove $SERVICE_NAME confirm"
	
	
	StrCpy $SVC_CP '-classpath "bin\.;lib\*;..\ImbekoWebAPILib\lib\*;..\Imbeko-Common\lib\*;..\Imbeko-Common\vert.x\lib\*"'
	StrCpy $SVC_CP '$SVC_CP -javaagent:..\Imbeko-Common\lib\ImbekoJmxAgent-1.1.00.jar'
	StrCpy $SVC_CP '$SVC_CP -XX:+UseG1GC -XX:+ParallelRefProcEnabled -Xmx4096m -Xms4096m'
	StrCpy $SVC_CP '$SVC_CP -Djava.library.path="bin/auth"'
	StrCpy $SVC_CP '$SVC_CP -XX:+UnlockCommercialFeatures -XX:+FlightRecorder'
	StrCpy $SVC_CP '$SVC_CP -Dcom.sun.management.jmxremote'
	StrCpy $SVC_CP '$SVC_CP -Dza.co.transactionjunction.imbeko.rmiregistry.port=$JMX_PORT'
	StrCpy $SVC_CP '$SVC_CP -Dcom.sun.management.jmxremote.local.only=false'
	StrCpy $SVC_CP '$SVC_CP -Dcom.sun.management.jmxremote.authenticate=false'
	StrCpy $SVC_CP '$SVC_CP -Dcom.sun.management.jmxremote.ssl=false'
	StrCpy $SVC_CP '$SVC_CP -Dhazelcast.jmx=true'
	StrCpy $SVC_CP '$SVC_CP -Dlogback.configurationFile="bin\logback.xml"'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.options.blockedThreadCheckInterval=1000'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.options.warningExceptionTime=2000000000'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.options.maxWorkerExecuteTime=10000000000'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.options.maxEventLoopExecuteTime=2000000000'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.metrics.options.enabled=true'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.metrics.options.jmxDomain=metrics'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.metrics.options.jmxEnabled=true'
	StrCpy $SVC_CP '$SVC_CP -Dvertx.metrics.options.registryName=${APP_NAME}'
	StrCpy $SVC_CP '$SVC_CP -Dhazelcast.logging.type=slf4j'
	StrCpy $SVC_CP '$SVC_CP -Dhazelcast.io.selectorMode=selectwithfix'
	StrCpy $SVC_CP '$SVC_CP -Xloggc:gc-${APP_NAME}.log'
	StrCpy $SVC_CP '$SVC_CP -XX:+PrintGCDateStamps'
	
	ExecWait '$NSSM_EXE install "$SERVICE_NAME" "$JRE_HOME\bin\$JAVA_EXE" $SVC_CP io.vertx.core.Launcher run $MAIN_VERTICLE_CLASS -conf bin/config.json -cluster'
	
	ExecWait '$NSSM_EXE set "$SERVICE_NAME" AppDirectory "$INSTDIR"'
	ExecWait '$NSSM_EXE set "$SERVICE_NAME" DisplayName $SERVICE_NAME'
	ExecWait '$NSSM_EXE set "$SERVICE_NAME" Description $SERVICE_NAME service'
	ExecWait '$NSSM_EXE set "$SERVICE_NAME" AppExit Default Exit'
	ExecWait '$NSSM_EXE set "$SERVICE_NAME" Start SERVICE_DEMAND_START'

SectionEnd

# Installer functions
Function .onInit
    InitPluginsDir
	
	;NSIS can't really print to console without hackery so we just open the messagebox anyway even in silent mode.
	IfFileExists "C:\TransactionJunction\Imbeko-Common" +3 0
		MessageBox MB_OK "ImbekoLib *must* be installed before ${APP_NAME} can be installed - Installation will be aborted."
		Abort
	
FunctionEnd

Function Welcome
    nsDialogs::Create 1018
    Pop $welcome_Dialog

    ${If} $welcome_Dialog == error
        Abort
    ${EndIf}
    
	${NSD_CreateGroupBox} 2u 2u -3u -6u ""
	Pop $0
	
	${NSD_CreateLabel} 10u 10u -20u -20u "Welcome to the ${APP_NAME} installer.$\n$\nNOTE: this installer does not currently support rollback.$\n$\nPlease back up your Imbeko installation (if any) before proceeding.$\n$\n"
	Pop $0
	
	nsDialogs::Show
FunctionEnd

Function DatabaseDetails
	nsDialogs::Create 1018
	Pop $db_details_Dialog

	${If} $db_details_Dialog == error
		Abort
	${EndIf}
	
	${NSD_CreateGroupBox} 1u 1u -3u -6u "Database Details"
	Pop $0
	
	${NSD_CreateLabel} 20u 22u 20% 14.15u "Server"
	Pop $0
	
	${NSD_CreateText} 90u 20u 60% 12.31u "localhost"
	Pop $db_server_hwnd
	
	;${NSD_CreateLabel} 20u 42u 20% 14.15u "Port"
	;Pop $0
	
	;${NSD_CreateText} 90u 40u 60% 12.31u ""
	;Pop $db_port_hwnd
	;${NSD_SetTextLimit} $db_port_hwnd 5
	
	${NSD_CreateLabel} 20u 62u 20% 14.15u "User"
	Pop $0
	
	${NSD_CreateText} 90u 60u 60% 12.31u ""
	Pop $db_user_hwnd
	
	${NSD_CreateLabel} 20u 82u 20% 14.15u "Password"
	Pop $0

	${NSD_CreatePassword} 90u 80u 60% 12.31u ""
	Pop $db_password_hwnd
	
	nsDialogs::Show
FunctionEnd

Function DatabaseDetailsEnd
	${NSD_GetText} $db_server_hwnd $db_server
	${If} $db_server == ""
    	MessageBox MB_ICONSTOP|MB_OK "Server must be specified for the database."
    	Abort
	${EndIf}
	;${NSD_GetText} $db_port_hwnd $db_port
	${NSD_GetText} $db_user_hwnd $db_user
	${NSD_GetText} $db_password_hwnd $db_password
	${If} $db_user == ""
		StrCpy $db_password ""
	${EndIf}
FunctionEnd



