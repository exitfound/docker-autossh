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











// pipeline { 
//     agent {
//         docker { 
//             image 'ubuntu:latest'
//             args '-u 0'
//         }
//     }
//        stages { 
//            stage ('Build') { 
//                steps { 
//                    sh 'apt-get update && apt-get install -y curl && curl https://www.google.com' 
//                }
//            }
//        }
//    }
