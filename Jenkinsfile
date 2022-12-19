pipeline {
    agent {
        label 'agent-1'
    }
    environment {
        REMOTE_HOST = '192.168.88.218'
    }
    stages { 
           stage ('Test') { 
               steps { 
                   sh 'cat /etc/passwd' 
               }
           }
       }
}
