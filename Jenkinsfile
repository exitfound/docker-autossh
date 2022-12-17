pipeline { 
    agent {
        docker { 
            image 'ubuntu:latest'
            args '-u 0'
        }
    }
       stages { 
           stage ('Build') { 
               steps { 
                   sh 'apt-get update && apt-get install -y curl && curl https://www.google.com' 
               }
           }
       }
   }
