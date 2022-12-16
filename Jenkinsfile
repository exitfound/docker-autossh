pipeline { 
    agent {
        docker 'nginx:latest'
    }
       stages { 
           stage ('Build') { 
               steps { 
                   sh 'curl http://localhost' 
               }
           }
       }
   }
