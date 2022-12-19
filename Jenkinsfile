pipeline {
    agent {
        label 'agent-1'
    }
    environment {
        REMOTE_HOST = '192.168.88.218'

    parameters {
        string(name: 'IMAGE_TAG', defaultValue: 'latest', trim: true, description: 'Тэг для образа autossh:')
        string(name: 'SSH_PORT', defaultValue: '22', trim: true, description: 'Порт SSH-сервера для подключения к даленному серверу:')
        string(name: 'SSH_USER', defaultValue: '', trim: true, description: 'Имя пользователя, которое будет использоваться для подключения:')
    }

    }
    stages { 
           stage ('Test') { 
               steps { 
                   sh 'cat /etc/passwd' 
               }
           }
       }
}
