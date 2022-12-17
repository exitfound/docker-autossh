pipeline { 
    agent {
        docker 'ubuntu:latest'
    }
       stages { 
           stage ('Build') { 
               steps { 
                   sh 'sudo apt update && sudo apt install -y curl && curl https://www.google.com' 
               }
           }
       }
   }
