pipeline { 
    agent {
        docker 'ubuntu:latest'
    }
       stages { 
           stage ('Build') { 
               steps { 
                   sh 'apt-get update && apt-get install -y curl && curl https://www.google.com' 
               }
           }
       }
   }
