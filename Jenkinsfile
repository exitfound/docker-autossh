pipeline { 
    agent {
        docker 'ubuntu'
    }
       stages { 
           stage ('Build') { 
               steps { 
                   sh 'apt update && apt install -y curl && curl https://www.google.com' 
               }
           }
       }
   }
