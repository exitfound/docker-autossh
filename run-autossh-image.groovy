pipeline {

    agent {
        label 'agent-1'
    }

    environment {
        DOCKERHUB_IMAGE = 'mdd13/jenkins-medaev'
        DOCKERHUB_TAG = 'latest'
    }

    parameters {
        // Параметры для подключения к удаленному серверу:
        string(name: 'SSH_USER', defaultValue: '', trim: true, description: 'Имя пользователя, которое будет использоваться для подключения к удаленному серверу:')
        string(name: 'SSH_HOST', defaultValue: '', trim: true, description: 'IP-адрес, который будет использоваться для подключения к удаленному серверу:')
        string(name: 'SSH_PORT', defaultValue: '22', trim: true, description: 'Порт SSH для подключения к удаленному серверу, на котором будет выполняться команда:')

        // Параметры для настройки туннеля:
        string(name: 'SSH_MODE', defaultValue: '-L', trim: true, description: 'Режим, в котором будет работать туннель (прямой или обратный). По умолчанию используется прямой:')        
        string(name: 'SSH_TUNNEL_REMOTE_PORT', defaultValue: '', trim: true, description: 'Удаленный порт, который будет фигурировать на конечном инстансе, к которому будет подключаться контейнер через Autossh:')
        string(name: 'SSH_TUNNEL_IP', defaultValue: '', trim: true, description: 'IP-адрес конечного инстанса, до которого будет прокинут туннель:')
        string(name: 'SSH_TUNNEL_LOCAL_PORT', defaultValue: '', trim: true, description: 'Локальный порт, представляющий собой какой-либо сервис, который мы хотим прокинуть на наш конечный инстанс:')
    }

    stages {
        stage ('Deploy Autossh Image') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_key_host', keyFileVariable: 'SSH_KEY_HOST')]){
                    sh '''
                    set +x
                    ssh -i $SSH_KEY_HOST -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} "sudo docker pull $DOCKERHUB_IMAGE:$DOCKERHUB_TAG"
                    '''
                }
            }
        }
    }

    post { 
        always { 
            cleanWs()
        }
    }

}