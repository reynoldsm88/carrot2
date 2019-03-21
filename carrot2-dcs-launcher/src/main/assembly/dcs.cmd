@ECHO OFF

REM Redirect the user to use the *.sh script under CygWin environment
REM (otherwise CTRL-C leaves subprocesses running).
IF DEFINED SHELL (
  IF DEFINED ORIGINAL_PATH (
    ECHO Use the bash launcher script ^(dcs.sh^) on CygWin instead of dcs.cmd
    EXIT /b 1
  )
)

SETLOCAL
TITLE DCS @version@

REM Determine installation home
IF NOT "%DCS_HOME%"=="" GOTO homeSet
SET DCS_HOME=%~dp0
:homeSet

REM Set other non-default options, if not set by the user.
IF NOT "%DCS_OPTS%"=="" GOTO optsSet
SET DCS_OPTS=
:optsSet

REM Launch DCS.
java %DCS_OPTS% -jar "%DCS_HOME%\lib\carrot2-dcs-launcher-@version@.jar" %*
SET DCS_EXITVAL=%errorlevel%

REM Set cmd's window title and return with the exit code.
TITLE %comspec%
exit /b %DCS_EXITVAL%
