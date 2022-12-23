pipeline {

    agent {
        label 'agent-1'
    }

    environment {
        DOCKERHUB_IMAGE = 'mdd13/jenkins-medaev'
        DOCKERHUB_TAG = 'latest'
    }

    parameters {
        // Параметры для подключения, если мы хотим организовать туннель на удаленном узле:
        string(name: 'SSH_USER', defaultValue: '', trim: true, description: 'Имя пользователя, которое будет использоваться для подключения к удаленному серверу:')
        string(name: 'SSH_HOST', defaultValue: '', trim: true, description: 'IP-адрес, который будет использоваться для подключения к удаленному серверу:')

        // Параметры для подключения к конечному серверу, откуда будет создаваться туннель:
        string(name: 'SSH_TUNNEL_USER', defaultValue: '', trim: true, description: 'Имя пользователя, которое будет использоваться для подключения к конечному серверу при создании туннеля:')
        string(name: 'SSH_TUNNEL_HOST', defaultValue: '', trim: true, description: 'IP-адрес, который будет использоваться для подключения к конечному серверу при создании туннеля:')
        string(name: 'SSH_TUNNEL_PORT', defaultValue: '22', trim: true, description: 'Порт SSH для подключения к конечному серверу при создании туннеля:')

        // Параметры для настройки туннеля:
        string(name: 'SSH_TUNNEL_MODE', defaultValue: '-L', trim: true, description: 'Режим, в котором будет работать туннель (прямой или обратный). По умолчанию используется прямой:')        
        string(name: 'SSH_TUNNEL_LOCALPORT', defaultValue: '', trim: true, description: 'Локальный порт, представляющий собой сервис, который будет проброшен с конечного инстанса:')
        string(name: 'SSH_TUNNEL_IP', defaultValue: '', trim: true, description: 'IP-адрес конечного инстанса, до которого будет прокинут туннель:')
        string(name: 'SSH_TUNNEL_REMOTEPORT', defaultValue: '', trim: true, description: 'Удаленный порт сервиса, который запущен на конечном инстансе и это тот сервис, который будет прокинут через Autossh:')
    }

    stages {
        stage ('Run Autossh Image') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_key_host', keyFileVariable: 'SSH_KEY_HOST')]){
                    sh '''
                    ssh -i $SSH_KEY_HOST -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} "sudo docker pull $DOCKERHUB_IMAGE:$DOCKERHUB_TAG \
                    && sudo docker rm -f $(docker ps | grep autossh | awk '{print $1}') && \
                    && sudo docker run -d \
                    -e SSH_TUNNEL_PORT="${SSH_TUNNEL_PORT}" \
                    -e SSH_TUNNEL_MODE="${SSH_TUNNEL_MODE}" \
                    -e SSH_TUNNEL_LOCALPORT="${SSH_TUNNEL_LOCALPORT}" \
                    -e SSH_TUNNEL_IP="${SSH_TUNNEL_IP}" \
                    -e SSH_TUNNEL_REMOTEPORT="${SSH_TUNNEL_REMOTEPORT}" \
                    -e SSH_TUNNEL_USER="${SSH_TUNNEL_USER}" \
                    -e SSH_TUNNEL_HOST="${SSH_TUNNEL_HOST}" \
                    --name autossh \
                    --network host \
                    --restart unless-stopped \
                    $DOCKERHUB_IMAGE:$DOCKERHUB_TAG"
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