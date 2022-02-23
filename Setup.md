# Setup

## requirements

1. VSCode with the following plugins: 
   1. 'Remote - Containers' 
   2. 'Docker'

2. Docker (on Windows WSL is recommended) 

## Setup steps 

1. Clone the repository 
2. Start frontend
   1. Open the root project in VSCode
   2. Press `strg+shift+p execute` Remote-Containers: Open Folder in Container... and select frontend
   3. wait until the installation is done 
   4. execute `npm start`
3. Start backend
   1. Open the root project  VSCode  
   2.  Press strg+shift+p execute Remote-Containers: Open Folder in Container... and select backend
   3. wait until the installation is done 
   4. execute `mvn spring-boot:run`

