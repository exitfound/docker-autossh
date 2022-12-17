pipeline { 
    agent {
        docker 'ubuntu:latest'
    }
       stages { 
           stage ('Build') { 
               steps { 
                   sh 'apt update && sudo install -y curl && curl https://www.google.com' 
               }
           }
       }
   }
