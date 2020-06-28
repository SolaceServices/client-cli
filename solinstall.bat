set "SOL_PATH=%cd%"
SET PATH=%PATH%;%SOL_PATH%
setx SOL_PATH %SOL_PATH% /m
REG delete "HKCU\SOFTWARE\Microsoft\Command Processor" /v "AutoRun" /t REG_EXPAND_SZ /d "%AppData%/solreg.cmd"