##
 # KIOSK INSTALLER
 # ---------------
 # William Chargin
 ##################


##
 # Define directories.
 ##
	!define DIR_EXE Windows/Applications
	!define DIR_RESOURCES nsis_rsc
	!define DIR_OUTPATH Windows
 
##
 # Include necessary libraries.
 ##

	!include "MUI2.nsh"
	!include "LogicLib.nsh"
	!include "${DIR_RESOURCES}\FileAssociation.nsh"


##
 # General installer setup.
 ##

	# Set installer output path.
	OutFile "${DIR_OUTPATH}\kiosk-3.1.exe"

	# Set default installation folder.
	InstallDir "$PROGRAMFILES\$(NAME_Application)"

	# If the installation directory is in the registry, copy it.
	InstallDirRegKey HKCU "Software\Kiosk III" ""

	# Request administrator access.
	RequestExecutionLevel admin

##
 # Define interface settings.
 ##

	!define MUI_ABORTWARNING
	!define MUI_ICON "${DIR_RESOURCES}\icon.ico"
	!define MUI_HEADERIMAGE
	!define MUI_HEADERIMAGE_BITMAP "${DIR_RESOURCES}\header.bmp"
	!define MUI_WELCOMEFINISHPAGE_BITMAP "${DIR_RESOURCES}\welcomefinish.bmp"
	!define MUI_UNWELCOMEFINISHPAGE_BITMAP "${DIR_RESOURCES}\unwelcomefinish.bmp"

##
 # Set up pages. 
 ##

	!insertmacro MUI_PAGE_WELCOME
	!insertmacro MUI_PAGE_LICENSE "$(License)"
	!insertmacro MUI_PAGE_COMPONENTS
	!insertmacro MUI_PAGE_DIRECTORY
	!insertmacro MUI_PAGE_INSTFILES
	
	!define MUI_FINISHPAGE_RUN "$INSTDIR\Kiosk.exe"
	!define MUI_FINISHPAGE_RUN_TEXT $(DESC_RunText)
	!define MUI_PAGE_CUSTOMFUNCTION_SHOW ModifyRunCheckbox

	!insertmacro MUI_PAGE_FINISH

	!insertmacro MUI_UNPAGE_WELCOME
	!insertmacro MUI_UNPAGE_CONFIRM
	!insertmacro MUI_UNPAGE_INSTFILES
	!insertmacro MUI_UNPAGE_FINISH

	!insertmacro MUI_LANGUAGE "English"
	!insertmacro MUI_LANGUAGE "Spanish"
	LicenseLangString License ${LANG_ENGLISH} "${DIR_RESOURCES}\License.txt"
	LicenseLangString License ${LANG_SPANISH} "${DIR_RESOURCES}\Licencia.txt"

##
 # Main methods.
 ##
Function .onInit
	!insertmacro MUI_LANGDLL_DISPLAY
FunctionEnd
Function un.onInit
	!insertmacro MUI_UNGETLANGUAGE
FunctionEnd

##
 # This macro is executed on all sections. It sets the output path, stores 
 # the path in the registry (for the uninstaller), and writes the uninstaller.
 ##
!macro common
	SetOutPath "$INSTDIR" 
	# Store installation folder.
	WriteRegStr HKCU "Software\Kiosk III" "" $INSTDIR

	# Create uninstaller.
	WriteUninstaller "$INSTDIR\Uninstall.exe"
!macroend

##
 # Defines the path for the Start Menu shortcut.
 ##
Var shortcutPath
Section -Hidden
StrCpy $shortcutPath "$SMPROGRAMS\$(NAME_Application)"
CreateDirectory "$shortcutPath"
SectionEnd

##
 # Set all localization strings.
 ##

	##
	 # English
	 ##
	LangString NAME_Application ${LANG_ENGLISH} "Kiosk"
	LangString NAME_SecCore ${LANG_ENGLISH} "Kiosk Core"
	LangString NAME_SecKiosk ${LANG_ENGLISH} "Kiosk"
	LangString NAME_SecAnalyzer ${LANG_ENGLISH} "Analyzer"
	LangString NAME_SecReceiver ${LANG_ENGLISH} "Receiver"
	LangString NAME_SecOmni ${LANG_ENGLISH} "OmniKiosk"
	LangString DESC_SecCore ${LANG_ENGLISH} "Core applications for committee directors to manage, analyze, and communicate."
	LangString DESC_SecKiosk ${LANG_ENGLISH} "Main Kiosk application for debate management."
	LangString DESC_SecAnalyzer ${LANG_ENGLISH} "Kiosk speech analyzer companion application."
	LangString DESC_SecReceiver ${LANG_ENGLISH} "Message receiver and alert system companion application."
	LangString DESC_SecOmni ${LANG_ENGLISH} "OmniKiosk observation system for Secretary-General."
	LangString DESC_RunText ${LANG_ENGLISH} "Start Kiosk now"
	LangString DESC_NotInstalled ${LANG_ENGLISH} "You have chosen not to install the main Kiosk program. If you wish to use it, please run the installer again."
	
	##
	 # Spanish
	 ##
	LangString NAME_Application ${LANG_SPANISH} "Quiosco"
	LangString NAME_SecCore ${LANG_SPANISH} "Corazón de quiosco"
	LangString NAME_SecKiosk ${LANG_SPANISH} "Quiosco"
	LangString NAME_SecAnalyzer ${LANG_SPANISH} "Analizador"
	LangString NAME_SecReceiver ${LANG_SPANISH} "Receptor"
	LangString NAME_SecOmni ${LANG_SPANISH} "OmniQuiosco"
	LangString DESC_SecCore ${LANG_SPANISH} "Programas claves para directores de comité para dirigir, analizar, y comunicar."
	LangString DESC_SecKiosk ${LANG_SPANISH} "Programa Quisoco principal para manejar debate."
	LangString DESC_SecAnalyzer ${LANG_SPANISH} "Programa secundario para analizar discursos."
	LangString DESC_SecReceiver ${LANG_SPANISH} "Receptor de mensajes y sistema de noticias."
	LangString DESC_SecOmni ${LANG_SPANISH} "OmniQuiosco, sistema de observación para Secretario-General."
	LangString DESC_RunText ${LANG_SPANISH} "Empezar Quiosco ahora"
	LangString DESC_NotInstalled ${LANG_SPANISH} "Eligió no instalar el programa principal de Quiosco. Si quiere usarlo, empieze el instalador de nuevo."

	##
	 # Set program name to localized string.
	 ## 
	Name $(NAME_Application)

##
 # This function is executed if the user finishes with the "Start Kiosk"
 # checkbox selected. It verifies that the file exists, and then opens
 # it. If it does not exist, it shows an error message.
 ##
Function ModifyRunCheckbox
	IfFileExists "$INSTDIR\Kiosk.exe" yes no
	yes:
		# File exists.
		# We don't need to change anything.
		Return
	no:
		# Uncheck the checkbox...
		SendMessage $mui.FinishPage.Run ${BM_SETCHECK} ${BST_UNCHECKED} 0
		
		# ...and disable it.
		EnableWindow $mui.FinishPage.Run 0 ; Or ShowWindow $mui.FinishPage.Run 0
		Return
FunctionEnd
	
##
 # Installer sections start here.
###
 # Core section (Kiosk for Committee Directors) contains Kiosk, Analzyer,
 # and Broadcast Receiver.
 ##
SectionGroup /E $(NAME_SecCore) SecCore

##
 # Main Kiosk application.
 ##
Section $(NAME_SecKiosk) SecKiosk
	!insertmacro common
	File ${DIR_EXE}\Kiosk.exe
	File ${DIR_RESOURCES}\UserManual.pdf
	CreateShortCut "$shortcutPath\Kiosk.lnk" "$INSTDIR\Kiosk.exe"
	${registerExtension} "$INSTDIR\Kiosk.exe" ".mun" "Model UN Committee"
SectionEnd

##
 # Analyzer application.
 ##
Section $(NAME_SecAnalyzer) SecAnalyzer
	!insertmacro common
	File ${DIR_EXE}\Analyzer.exe
	CreateShortCut "$shortcutPath\Speech Analyzer.lnk" "$INSTDIR\Analyzer.exe"
	${registerExtension} "$INSTDIR\Analyzer.exe" ".sai" "Speech Analysis Index"
SectionEnd

##
 # Broadcast Receiver application.
 ##
Section $(NAME_SecReceiver) SecReceiver
	!insertmacro common
	File ${DIR_EXE}\Receiver.exe
	CreateShortCut "$shortcutPath\Broadcast Receiver.lnk" "$INSTDIR\Receiver.exe"	
SectionEnd

SectionGroupEnd

##
 # OmniKiosk application.
 ##
Section /O $(NAME_SecOmni) SecOmni
	!insertmacro common
	File ${DIR_EXE}\OmniKiosk.exe
	CreateShortCut "$shortcutPath\OmniKiosk.lnk" "$INSTDIR\OmniKiosk.exe"
SectionEnd

##
 # Assign localization strings to section descriptions.
 ##
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${SecCore} $(DESC_SecCore)
	!insertmacro MUI_DESCRIPTION_TEXT ${SecKiosk} $(DESC_SecKiosk)
	!insertmacro MUI_DESCRIPTION_TEXT ${SecAnalyzer} $(DESC_SecAnalyzer)
	!insertmacro MUI_DESCRIPTION_TEXT ${SecReceiver} $(DESC_SecReceiver)
	!insertmacro MUI_DESCRIPTION_TEXT ${SecOmni} $(DESC_SecOmni)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

##
 # Uninstaller section. This will remove all installed files, including the
 # uninstaller, and remove the registry key.
 ##
Section "Uninstall"

	# Delete uninstaller.
	Delete "$INSTDIR\Uninstall.exe"

	# Delete files.
	Delete "$INSTDIR\Kiosk.exe"
	Delete "$INSTDIR\UserManual.pdf"
	Delete "$INSTDIR\Analyzer.exe"
	Delete "$INSTDIR\Receiver.exe"
	Delete "$INSTDIR\OmniKiosk.exe"
	
	# Unregister extensions.
	${unregisterExtension} ".mun" "Model UN Committee"
	${unregisterExtension} ".sai" "Speech Analysis Index"
	
	# Remove shortcuts.
	Delete "$shortcutPath\Kiosk.lnk"
	Delete "$shortcutPath\Speech Analyzer.lnk"
	Delete "$shortcutPath\Broadcast Receiver.lnk"
	Delete "$shortcutPath\OmniKiosk.lnk"
	
	# Remove now empty directory.
	RMDir "$INSTDIR"
	
	# Delete installation director registry key.
	DeleteRegKey /IfEmpty HKCU "Software\Kiosk III"
	
SectionEnd
