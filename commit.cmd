@echo off
setlocal enabledelayedexpansion
echo ========================================
echo Git Auto Commit Script
echo ========================================
echo.

REM Read commit message from file
if not exist "commit-message.txt" (
    echo ERROR: commit-message.txt not found!
    echo Please create commit-message.txt with your commit message.
    pause
    exit /b 1
)

set /p COMMIT_MSG=<commit-message.txt
if "!COMMIT_MSG!"=="" (
    echo ERROR: commit-message.txt is empty!
    pause
    exit /b 1
)

echo Commit message: !COMMIT_MSG!
echo.

echo [1/5] Adding all changes...
git add .

echo [2/5] Unstaging excluded files...
git restore --staged src/main/resources/application.properties
git restore --staged commit.cmd
git restore --staged commit-message.txt

echo [3/5] Current status:
git status --short
echo.

echo [4/5] Committing changes...
git commit -m "!COMMIT_MSG!"

echo [5/5] Pushing to remote...
git push origin main

echo.
echo ========================================
echo Done! Press any key to exit...
echo ========================================
pause > nul