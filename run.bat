@echo off

REM Define lists as comma-separated values
set HOSTS=fa24-cs425-a605.cs.illinois.edu
set IPS=172.22.159.98
set PORTS1=5005
set PORTS2=6005
set NAMES=Machine5

REM Define variables
set VM_USER=sdare1
set /p VM_PASSWORD=< password.txt

REM Split the lists into arrays and iterate over them
setlocal enabledelayedexpansion
set index=0

REM Convert lists to arrays
for %%H in (%HOSTS%) do (
    set /a index+=1
    set HOST[!index!]=%%H
)
set index=0
for %%I in (%IPS%) do (
    set /a index+=1
    set IP[!index!]=%%I
)
set index=0
for %%P in (%PORTS1%) do (
    set /a index+=1
    set PORT1[!index!]=%%P
)
set index=0
for %%P in (%PORTS2%) do (
    set /a index+=1
    set PORT2[!index!]=%%P
)
set index=0
for %%N in (%NAMES%) do (
    set /a index+=1
    set NAME[!index!]=%%N
)

REM Determine the number of elements
set /a max_index=%index%

REM Iterate through the arrays
for /L %%i in (1,1,%max_index%) do (
    set VM_HOST=!HOST[%%i]!
    set MACHINE_IP=!IP[%%i]!
    set PORT_NUMBER1=!PORT1[%%i]!
    set PORT_NUMBER2=!PORT2[%%i]!
    set MACHINE_NAME=!NAME[%%i]!

    echo Connecting to !VM_HOST!...

    REM Connect to the VM and run the commands
    (
        echo y
        echo.
    ) | plink %VM_USER%@!VM_HOST! -l %VM_USER% -pw %VM_PASSWORD% ^
        "pwd && " ^
        "mkdir Distributed && " ^
        "cd Distributed && " ^
        "git init && " ^
        "git config user.name 'sdare1' && " ^
        "git config user.email 'sdare1@illinois.edu' && " ^
        "git clone https://%VM_USER%:%VM_PASSWORD%@gitlab.engr.illinois.edu/sdare1/GA6-CS-425-MP2.git && " ^
        "ls && " ^
        "cd GA6-CS-425-MP2 && " ^
        "mvn install -DskipTests && " ^
        "echo 'machineIp=!MACHINE_IP!' >> application.properties && " ^
        "echo 'port.number=!PORT_NUMBER1!' >> application.properties && " ^
        "echo 'machineName=!MACHINE_NAME!' >> application.properties && " ^
        "echo 'machinePort=!PORT_NUMBER2!' >> application.properties && " ^
        "cd target/ && " ^
        "mv mp1-1.jar ../"

    REM Check the exit status
    if %errorlevel% equ 0 (
        echo Commands executed successfully on !VM_HOST!.
    ) else (
        echo Failed to execute commands on !VM_HOST!.
    )

    echo ---------------------------------
)

echo Script execution completed.
pause
