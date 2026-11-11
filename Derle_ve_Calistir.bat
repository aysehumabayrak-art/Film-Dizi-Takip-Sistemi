@echo off
chcp 65001 > nul
cd /d "%~dp0\src"

:: Java derleyici kontrolü ve JDK yolu arama
set "JAVAC_CMD=javac"
where javac >nul 2>&1
if %errorlevel% neq 0 (
    if exist "C:\Program Files\Java\jdk-27.0.1.0.3\bin\javac.exe" (
        set "PATH=C:\Program Files\Java\jdk-27.0.1.0.3\bin;%PATH%"
    ) else if exist "C:\Program Files\Java\latest\bin\javac.exe" (
        set "PATH=C:\Program Files\Java\latest\bin;%PATH%"
    )
)

echo ===================================
echo 1. Eski Sinif Dosyalari Temizleniyor...
del /s /q *.class 2>nul

echo 2. Tum Paketler Derleniyor...
javac -encoding UTF-8 com\takip\model\*.java com\takip\gui\*.java com\takip\*.java

if %errorlevel% neq 0 (
    echo.
    echo -----------------------------------
    echo DERLEME HATASI OLUSTU!
    echo Yukaridaki kirmizi/beyaz hata satirini inceleyin.
    echo -----------------------------------
    pause
    exit /b
)

echo 3. Program Baslatiliyor...
java com.takip.Main
pause